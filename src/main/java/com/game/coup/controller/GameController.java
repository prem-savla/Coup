// package com.game.coup.controller;

// import com.game.coup.dto.GameFlowRequest;
// import com.game.coup.dto.debug.GameDebugResponse;
// import com.game.coup.service.GameService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api")
// @RequiredArgsConstructor
// public class GameController {

//     private final GameService gameService;

//     @PostMapping("/{roomId}/play")
//     public void handleFlow(
//             @PathVariable String roomId,
//             @Valid @RequestBody GameFlowRequest request
//     ) {
//         if (!roomId.equals(request.getRoomId())) {
//             throw new IllegalArgumentException("RoomId mismatch");
//         }

//         gameService.handleFlow(request);
//     }

//     @GetMapping("/{roomId}/options")
//     public Object getOptions(@PathVariable String roomId) {
//         return gameService.getOptions(roomId);
//     }

//     @GetMapping("/{roomId}/debug")
//     public GameDebugResponse debug(@PathVariable String roomId) {
//         return gameService.getDebugState(roomId);
//     }
// }