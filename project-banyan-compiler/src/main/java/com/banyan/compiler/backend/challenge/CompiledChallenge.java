package com.banyan.compiler.backend.challenge;

import java.util.List;

/*
{
  "kind": "Challenge",
  "id": "unique_task_challenge",
  "version": 1,
  "spec": {
    "tasks": [
      { "id": "task_one","version":1},
     { "id": "task_two","version":1},
    ]
  }
}

 */
public record CompiledChallenge(
        List<CompiledTaskRef> compiledTaskRefsList
) {

}
