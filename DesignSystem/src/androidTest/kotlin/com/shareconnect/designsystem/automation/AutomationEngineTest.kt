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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for AutomationEngine
 */
@RunWith(AndroidJUnit4::class)
class AutomationEngineTest {

    private lateinit var engine: AutomationEngine

    @Before
    fun setup() {
        engine = AutomationEngine(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun initialization_succeeds() {
        assertNotNull(engine)
        assertTrue(engine.rules.value.isEmpty())
        assertTrue(engine.activeRules.value.isEmpty())
    }

    @Test
    fun addRule_addsToRulesList() = runBlocking {
        val rule = AutomationRule(
            id = "test_rule",
            name = "Test Rule",
            description = "Test automation rule",
            connector = "TestConnector",
            trigger = Trigger.ConnectionLost,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Test", "Message")),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)

        assertEquals(1, engine.rules.value.size)
        assertEquals("test_rule", engine.rules.value[0].id)
    }

    @Test
    fun removeRule_removesFromRulesList() = runBlocking {
        val rule = AutomationRule(
            id = "test_rule",
            name = "Test Rule",
            description = "Test",
            connector = "TestConnector",
            trigger = Trigger.ConnectionLost,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Test", "Message")),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)
        assertEquals(1, engine.rules.value.size)

        engine.removeRule("test_rule")
        delay(50)
        assertEquals(0, engine.rules.value.size)
    }

    @Test
    fun setRuleEnabled_updatesRuleState() = runBlocking {
        val rule = AutomationRule(
            id = "test_rule",
            name = "Test Rule",
            description = "Test",
            connector = "TestConnector",
            trigger = Trigger.ConnectionLost,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Test", "Message")),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)
        assertTrue(engine.rules.value[0].enabled)

        engine.setRuleEnabled("test_rule", false)
        delay(50)
        assertFalse(engine.rules.value[0].enabled)
    }

    @Test
    fun evaluateTrigger_withMatchingRule_executesActions() = runBlocking {
        val trigger = Trigger.AlarmTriggered(AlarmSeverity.CRITICAL)
        val rule = AutomationRule(
            id = "alarm_rule",
            name = "Alarm Rule",
            description = "Trigger on critical alarm",
            connector = "TestConnector",
            connector = "TestConnector",
            trigger = trigger,
            conditions = emptyList(),
            actions = listOf(
                Action.SendNotification("Automation","Critical alarm detected!")
            ),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)

        engine.evaluateTrigger(trigger, emptyMap())
        delay(100) // Give time for async execution

        // Rule should be in active rules after execution
        assertTrue(engine.activeRules.value.contains("alarm_rule"))
    }

    @Test
    fun evaluateTrigger_withConditions_evaluatesCorrectly() = runBlocking {
        val trigger = Trigger.MetricThreshold("cpu", 80.0)
        val rule = AutomationRule(
            id = "cpu_rule",
            name = "CPU Threshold Rule",
            description = "Alert when CPU > 90%",
            connector = "TestConnector",
            trigger = trigger,
            conditions = listOf(
                Condition("cpu_value", ConditionOperator.GREATER_THAN, 90)
            ),
            actions = listOf(
                Action.SendNotification("Automation","CPU too high!")
            ),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)

        // Test with CPU < 90 - should not execute
        engine.evaluateTrigger(trigger, mapOf("cpu_value" to 85))
        delay(100)
        assertFalse(engine.activeRules.value.contains("cpu_rule"))

        // Test with CPU > 90 - should execute
        engine.evaluateTrigger(trigger, mapOf("cpu_value" to 95))
        delay(100)
        assertTrue(engine.activeRules.value.contains("cpu_rule"))
    }

    @Test
    fun evaluateTrigger_disabledRule_doesNotExecute() = runBlocking {
        val trigger = Trigger.ConnectionLost
        val rule = AutomationRule(
            id = "disabled_rule",
            name = "Disabled Rule",
            description = "This rule is disabled",
            connector = "TestConnector",
            trigger = trigger,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Automation","Should not execute")),
            enabled = false
        )

        engine.addRule(rule)
        delay(50)

        engine.evaluateTrigger(trigger, emptyMap())
        delay(100)

        assertFalse(engine.activeRules.value.contains("disabled_rule"))
    }

    @Test
    fun conditionOperators_equalityWorks() = runBlocking {
        val trigger = Trigger.EntityStateChanged("light.living_room")

        // Test EQUALS
        val equalsRule = AutomationRule(
            id = "equals_rule",
            name = "Equals Rule",
            description = "Test equals",
            connector = "TestConnector",
            trigger = trigger,
            conditions = listOf(
                Condition("state", ConditionOperator.EQUALS, "on")
            ),
            actions = listOf(Action.SendNotification("Automation","State is on")),
            enabled = true
        )

        engine.addRule(equalsRule)
        delay(50)

        engine.evaluateTrigger(trigger, mapOf("state" to "on"))
        delay(100)
        assertTrue(engine.activeRules.value.contains("equals_rule"))
    }

    @Test
    fun conditionOperators_comparisonWorks() = runBlocking {
        val trigger = Trigger.MetricThreshold("temperature", 25.0)

        // Test GREATER_THAN
        val gtRule = AutomationRule(
            id = "gt_rule",
            name = "Greater Than Rule",
            description = "Test >",
            connector = "TestConnector",
            trigger = trigger,
            conditions = listOf(
                Condition("temp", ConditionOperator.GREATER_THAN, 20)
            ),
            actions = listOf(Action.SendNotification("Automation","Temp > 20")),
            enabled = true
        )

        engine.addRule(gtRule)
        delay(50)

        engine.evaluateTrigger(trigger, mapOf("temp" to 25))
        delay(100)
        assertTrue(engine.activeRules.value.contains("gt_rule"))
    }

    @Test
    fun conditionOperators_stringContainsWorks() = runBlocking {
        val trigger = Trigger.PlaybackStarted("movie")

        val containsRule = AutomationRule(
            id = "contains_rule",
            name = "Contains Rule",
            description = "Test contains",
            connector = "TestConnector",
            trigger = trigger,
            conditions = listOf(
                Condition("title", ConditionOperator.CONTAINS, "Inception")
            ),
            actions = listOf(Action.SendNotification("Automation","Playing Inception")),
            enabled = true
        )

        engine.addRule(containsRule)
        delay(50)

        engine.evaluateTrigger(trigger, mapOf("title" to "Inception 2010"))
        delay(100)
        assertTrue(engine.activeRules.value.contains("contains_rule"))
    }

    @Test
    fun multipleConditions_allMustMatch() = runBlocking {
        val trigger = Trigger.ContainerStateChanged(ContainerState.RUNNING)

        val rule = AutomationRule(
            id = "multi_condition",
            name = "Multi Condition Rule",
            description = "All conditions must match",
            connector = "TestConnector",
            trigger = trigger,
            conditions = listOf(
                Condition("cpu", ConditionOperator.LESS_THAN, 50),
                Condition("memory", ConditionOperator.LESS_THAN, 1000)
            ),
            actions = listOf(Action.SendNotification("Automation","All conditions met")),
            enabled = true
        )

        engine.addRule(rule)
        delay(50)

        // Only one condition matches - should not execute
        engine.evaluateTrigger(trigger, mapOf("cpu" to 30, "memory" to 2000))
        delay(100)
        assertFalse(engine.activeRules.value.contains("multi_condition"))

        // Both conditions match - should execute
        engine.evaluateTrigger(trigger, mapOf("cpu" to 30, "memory" to 500))
        delay(100)
        assertTrue(engine.activeRules.value.contains("multi_condition"))
    }

    @Test
    fun multipleRules_canBeActive() = runBlocking {
        val trigger = Trigger.ConnectionRestored

        val rule1 = AutomationRule(
            id = "rule1",
            name = "Rule 1",
            description = "First rule",
            connector = "TestConnector",
            trigger = trigger,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Automation","Rule 1")),
            enabled = true
        )

        val rule2 = AutomationRule(
            id = "rule2",
            name = "Rule 2",
            description = "Second rule",
            connector = "TestConnector",
            trigger = trigger,
            conditions = emptyList(),
            actions = listOf(Action.SendNotification("Automation","Rule 2")),
            enabled = true
        )

        engine.addRule(rule1)
        engine.addRule(rule2)
        delay(50)

        engine.evaluateTrigger(trigger, emptyMap())
        delay(100)

        assertTrue(engine.activeRules.value.contains("rule1"))
        assertTrue(engine.activeRules.value.contains("rule2"))
        assertEquals(2, engine.activeRules.value.size)
    }
}
