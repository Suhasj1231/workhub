package com.smj.workhub.notification.controller;

import com.smj.workhub.notification.dto.NotificationResponse;
import com.smj.workhub.notification.service.NotificationService;
import com.smj.workhub.security.principal.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications for current user")
    @GetMapping
    public Page<NotificationResponse> getAllNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return notificationService.getUserNotifications(user.getId(), pageable);
    }

    @Operation(summary = "Get unread notifications")
    @GetMapping("/unread")
    public Page<NotificationResponse> getUnreadNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return notificationService.getUnreadNotifications(user.getId(), pageable);
    }

    @Operation(summary = "Get unread notification count")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCount(user.getId()));
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        notificationService.markAsRead(notificationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mark all notifications as read")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
