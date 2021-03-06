package org.infinispan.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.infinispan.tasks.spi.TaskEngine;

public class DummyTaskEngine implements TaskEngine {

   static enum DummyTaskTypes {
      SUCCESSFUL_TASK, FAILING_TASK, SLOW_TASK
   }

   final Set<String> tasks;
   final CompletableFuture<String> slow;

   public DummyTaskEngine() {
      tasks = new HashSet<>();
      for (DummyTaskTypes type : DummyTaskTypes.values()) {
         tasks.add(type.toString());
      }
      slow = new CompletableFuture<>();
   }

   @Override
   public String getName() {
      return "Dummy";
   }

   @Override
   public List<Task> getTasks() {
      List<Task> taskDetails = new ArrayList<>();
      tasks.forEach(task -> {
         taskDetails.add(new DummyTask(task));
      });
      return taskDetails;
   }

   @Override
   public <T> CompletableFuture<T> runTask(String taskName, TaskContext context) {
      switch (DummyTaskTypes.valueOf(taskName)) {
      case SUCCESSFUL_TASK:
         return (CompletableFuture<T>) CompletableFuture.completedFuture("result");
      case FAILING_TASK:
         CompletableFuture<T> f = new CompletableFuture<>();
         f.completeExceptionally(new Exception("exception"));
         return f;
      case SLOW_TASK:
         return (CompletableFuture<T>) slow;
      }
      throw new IllegalArgumentException();
   }

   @Override
   public boolean handles(String taskName) {
      return tasks.contains(taskName);
   }

}
