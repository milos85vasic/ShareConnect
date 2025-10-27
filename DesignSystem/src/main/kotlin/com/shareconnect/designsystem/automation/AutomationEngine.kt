/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.designsystem.automation

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Automation Engine
 * Rule-based automation system for ShareConnect connectors
 */
class AutomationEngine(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {

    private val _rules = MutableStateFlow<List<AutomationRule>>(emptyList())
    val rules: StateFlow<List<AutomationRule>> = _rules.asStateFlow()

    private val _activeRules = MutableStateFlow<List<String>>(emptyList())
    val activeRules: StateFlow<List<String>> = _activeRules.asStateFlow()

    /**
     * Add automation rule
     */
    fun addRule(rule: AutomationRule) {
        _rules.value = _rules.value + rule
    }

    /**
     * Remove automation rule
     */
    fun removeRule(ruleId: String) {
        _rules.value = _rules.value.filterNot { it.id == ruleId }
    }

    /**
     * Enable/disable rule
     */
    fun setRuleEnabled(ruleId: String, enabled: Boolean) {
        _rules.value = _rules.value.map {
            if (it.id == ruleId) it.copy(enabled = enabled) else it
        }
    }

    /**
     * Evaluate trigger and execute actions if conditions met
     */
    fun evaluateTrigger(trigger: Trigger, data: Map<String, Any>) {
        scope.launch {
            val matchingRules = _rules.value.filter { rule ->
                rule.enabled && rule.trigger == trigger
            }

            matchingRules.forEach { rule ->
                if (evaluateConditions(rule.conditions, data)) {
                    executeActions(rule.actions, data)
                    _activeRules.value = _activeRules.value + rule.id
                }
            }
        }
    }

    /**
     * Evaluate all conditions
     */
    private fun evaluateConditions(
        conditions: List<Condition>,
        data: Map<String, Any>
    ): Boolean {
        return conditions.all { condition ->
            evaluateCondition(condition, data)
        }
    }

    /**
     * Evaluate single condition
     */
    private fun evaluateCondition(
        condition: Condition,
        data: Map<String, Any>
    ): Boolean {
        val value = data[condition.key] ?: return false

        return when (condition.operator) {
            ConditionOperator.EQUALS -> value == condition.value
            ConditionOperator.NOT_EQUALS -> value != condition.value
            ConditionOperator.GREATER_THAN -> {
                (value as? Number)?.toDouble()?.let { it > (condition.value as Number).toDouble() } ?: false
            }
            ConditionOperator.LESS_THAN -> {
                (value as? Number)?.toDouble()?.let { it < (condition.value as Number).toDouble() } ?: false
            }
            ConditionOperator.GREATER_THAN_OR_EQUALS -> {
                (value as? Number)?.toDouble()?.let { it >= (condition.value as Number).toDouble() } ?: false
            }
            ConditionOperator.LESS_THAN_OR_EQUALS -> {
                (value as? Number)?.toDouble()?.let { it <= (condition.value as Number).toDouble() } ?: false
            }
            ConditionOperator.CONTAINS -> {
                (value as? String)?.contains(condition.value.toString(), ignoreCase = true) ?: false
            }
            ConditionOperator.STARTS_WITH -> {
                (value as? String)?.startsWith(condition.value.toString(), ignoreCase = true) ?: false
            }
            ConditionOperator.ENDS_WITH -> {
                (value as? String)?.endsWith(condition.value.toString(), ignoreCase = true) ?: false
            }
        }
    }

    /**
     * Execute all actions
     */
    private fun executeActions(
        actions: List<Action>,
        data: Map<String, Any>
    ) {
        actions.forEach { action ->
            executeAction(action, data)
        }
    }

    /**
     * Execute single action
     */
    private fun executeAction(
        action: Action,
        data: Map<String, Any>
    ) {
        when (action) {
            is Action.SendNotification -> {
                // Notification handled by ConnectorNotificationManager
                action.execute(context, data)
            }
            is Action.UpdateWidget -> {
                action.execute(context, data)
            }
            is Action.CallService -> {
                action.execute(context, data)
            }
            is Action.RunScript -> {
                action.execute(context, data)
            }
            is Action.Delay -> {
                action.execute(context, data)
            }
        }
    }

    /**
     * Get rules by connector
     */
    fun getRulesByConnector(connectorName: String): List<AutomationRule> {
        return _rules.value.filter { rule ->
            rule.connector == connectorName
        }
    }

    /**
     * Get enabled rules
     */
    fun getEnabledRules(): List<AutomationRule> {
        return _rules.value.filter { it.enabled }
    }
}

/**
 * Automation rule
 */
data class AutomationRule(
    val id: String,
    val name: String,
    val description: String,
    val connector: String,
    val trigger: Trigger,
    val conditions: List<Condition>,
    val actions: List<Action>,
    val enabled: Boolean = true
)

/**
 * Trigger types
 */
sealed class Trigger {
    // Netdata triggers
    data class AlarmTriggered(val severity: AlarmSeverity) : Trigger()
    data class MetricThreshold(val metric: String, val threshold: Double) : Trigger()

    // Portainer triggers
    data class ContainerStateChanged(val state: ContainerState) : Trigger()
    data class ImagePulled(val imageName: String) : Trigger()

    // Jellyfin triggers
    data class PlaybackStarted(val mediaType: String) : Trigger()
    data class PlaybackStopped(val mediaType: String) : Trigger()

    // HomeAssistant triggers
    data class EntityStateChanged(val entityId: String) : Trigger()
    data class ServiceCalled(val serviceName: String) : Trigger()

    // Common triggers
    object ConnectionLost : Trigger()
    object ConnectionRestored : Trigger()
    data class TimeSchedule(val cronExpression: String) : Trigger()
}

enum class AlarmSeverity {
    CRITICAL, WARNING, INFO
}

enum class ContainerState {
    RUNNING, STOPPED, PAUSED, DEAD
}

/**
 * Condition
 */
data class Condition(
    val key: String,
    val operator: ConditionOperator,
    val value: Any
)

enum class ConditionOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUALS,
    LESS_THAN_OR_EQUALS,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH
}

/**
 * Action types
 */
sealed class Action {
    abstract fun execute(context: Context, data: Map<String, Any>)

    data class SendNotification(
        val title: String,
        val message: String,
        val priority: NotificationPriority = NotificationPriority.DEFAULT
    ) : Action() {
        override fun execute(context: Context, data: Map<String, Any>) {
            // Implementation would use ConnectorNotificationManager
        }
    }

    data class UpdateWidget(
        val widgetId: String
    ) : Action() {
        override fun execute(context: Context, data: Map<String, Any>) {
            // Implementation would trigger widget update
        }
    }

    data class CallService(
        val serviceName: String,
        val parameters: Map<String, Any>
    ) : Action() {
        override fun execute(context: Context, data: Map<String, Any>) {
            // Implementation would call connector service
        }
    }

    data class RunScript(
        val script: String
    ) : Action() {
        override fun execute(context: Context, data: Map<String, Any>) {
            // Implementation would execute script
        }
    }

    data class Delay(
        val milliseconds: Long
    ) : Action() {
        override fun execute(context: Context, data: Map<String, Any>) {
            Thread.sleep(milliseconds)
        }
    }
}

enum class NotificationPriority {
    LOW, DEFAULT, HIGH
}

/**
 * Automation rule builder
 */
class AutomationRuleBuilder {
    private var id: String = java.util.UUID.randomUUID().toString()
    private var name: String = ""
    private var description: String = ""
    private var connector: String = ""
    private var trigger: Trigger? = null
    private val conditions = mutableListOf<Condition>()
    private val actions = mutableListOf<Action>()
    private var enabled: Boolean = true

    fun id(id: String) = apply { this.id = id }
    fun name(name: String) = apply { this.name = name }
    fun description(description: String) = apply { this.description = description }
    fun connector(connector: String) = apply { this.connector = connector }
    fun trigger(trigger: Trigger) = apply { this.trigger = trigger }
    fun condition(condition: Condition) = apply { conditions.add(condition) }
    fun action(action: Action) = apply { actions.add(action) }
    fun enabled(enabled: Boolean) = apply { this.enabled = enabled }

    fun build(): AutomationRule {
        require(name.isNotBlank()) { "Rule name is required" }
        require(connector.isNotBlank()) { "Connector name is required" }
        require(trigger != null) { "Trigger is required" }
        require(actions.isNotEmpty()) { "At least one action is required" }

        return AutomationRule(
            id = id,
            name = name,
            description = description,
            connector = connector,
            trigger = trigger!!,
            conditions = conditions,
            actions = actions,
            enabled = enabled
        )
    }
}

/**
 * DSL for creating automation rules
 */
fun automationRule(block: AutomationRuleBuilder.() -> Unit): AutomationRule {
    return AutomationRuleBuilder().apply(block).build()
}

/**
 * Example usage:
 *
 * val rule = automationRule {
 *     name("High CPU Alert")
 *     description("Send notification when CPU exceeds 90%")
 *     connector("Netdata")
 *     trigger(Trigger.MetricThreshold("cpu", 90.0))
 *     condition(Condition("cpu", ConditionOperator.GREATER_THAN, 90))
 *     action(Action.SendNotification(
 *         title = "High CPU Usage",
 *         message = "CPU usage is above 90%",
 *         priority = NotificationPriority.HIGH
 *     ))
 *     action(Action.UpdateWidget("netdata_widget"))
 * }
 */
