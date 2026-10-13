package com.taskmanager.backendnew.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.taskmanager.backendnew.entity.Task;
import com.taskmanager.backendnew.entity.TaskPriority;
import com.taskmanager.backendnew.entity.TaskStatus;

public class TaskScheduler {

    public static List<Task> schedule(List<Task> tasks, LocalDateTime now) {

        // in this below for each task in the priority queue we are finding there score
        // and then comparing the score with other tasks
        // if the two tasks has equal score then compare there ids
        // create a priority queue of Tasks where the smallest score comes first, and if
        // two scores are equal, the smaller ID comes first.
        PriorityQueue<Task> heap = new PriorityQueue<>(
                Comparator.comparingLong((Task t) -> score(t, now)).thenComparing(Task::getId));

        // skipping the completed tasks
        // now here if the task is not completed we are adding it in the priorityQueue
        // according to our comparator
        // the priority queue ensures the minimum-score task is available first
        for (Task t : tasks) {
            if (t.getStatus() != TaskStatus.COMPLETED) {
                // adds the element
                heap.offer(t);
            }
        }

        List<Task> ordered = new ArrayList<>();
        while (!heap.isEmpty()) {
            // removes and returns the highest-priority element according to your
            // comparator
            ordered.add(heap.poll());
        }
        System.out.println("scheduled 334 done-=-=-=-=-");
        return ordered;
    }

    // lower score=more urgent
    static long score(Task t, LocalDateTime now) {
        if (t.getDeadline() == null)
            return Long.MAX_VALUE / 2;
        long hoursLeft = Duration.between(now, t.getDeadline()).toHours(); // negative if overdue
        long bonus = (t.getPriority() == null) ? 0 : t.getPriority().getBonusHours();
        return hoursLeft - bonus;
    }

    // private static long priorityBonus(TaskPriority p) {

    // if (p == null)
    // return 0;

    // return switch (p) {
    // case URGENT -> 80;
    // case HIGH -> 72;
    // case MEDIUM -> 24;
    // case LOW -> 10;
    // default -> 0;
    // };
    // }
}
