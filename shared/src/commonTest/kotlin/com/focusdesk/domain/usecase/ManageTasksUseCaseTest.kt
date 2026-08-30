package com.focusdesk.domain.usecase

import com.focusdesk.data.repository.InMemoryTaskRepository
import com.focusdesk.domain.model.TaskCategory
import com.focusdesk.domain.model.TaskPriority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ManageTasksUseCaseTest {

    private lateinit var taskRepository: InMemoryTaskRepository
    private lateinit var manageTasksUseCase: ManageTasksUseCase

    @BeforeTest
    fun setUp() {
        taskRepository = InMemoryTaskRepository()
        manageTasksUseCase = ManageTasksUseCase(taskRepository)
    }

    @Test
    fun `test creating a task adds item to list`() = runTest {
        val initialCount = manageTasksUseCase.observeAllTasks().first().size

        val newTask = manageTasksUseCase.createTask(
            title = "Write Unit Tests",
            description = "Test domain use cases thoroughly",
            priority = TaskPriority.Urgent,
            category = TaskCategory.Coding,
            estimatedPomodoros = 2
        )

        val updatedList = manageTasksUseCase.observeAllTasks().first()
        assertEquals(initialCount + 1, updatedList.size)
        assertTrue(updatedList.any { it.id == newTask.id })
    }

    @Test
    fun `test toggling task completion`() = runTest {
        val task = manageTasksUseCase.createTask(
            title = "Verification Task",
            description = "Toggle complete",
            priority = TaskPriority.Medium,
            category = TaskCategory.Writing,
            estimatedPomodoros = 1
        )
        assertFalse(task.isCompleted)

        manageTasksUseCase.toggleTask(task.id)
        val updated = taskRepository.getTask(task.id)
        assertTrue(updated?.isCompleted == true)
    }

    @Test
    fun `test deleting a task removes item`() = runTest {
        val task = manageTasksUseCase.createTask(
            title = "Temporary Task",
            description = "",
            priority = TaskPriority.Low,
            category = TaskCategory.Planning,
            estimatedPomodoros = 1
        )

        manageTasksUseCase.deleteTask(task.id)
        val list = manageTasksUseCase.observeAllTasks().first()
        assertFalse(list.any { it.id == task.id })
    }
}
