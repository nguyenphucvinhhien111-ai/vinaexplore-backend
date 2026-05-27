package com.vinatour.backend.constant;

import lombok.Getter;

@Getter
public enum NotificationType {
    LOCATION_APPROVED("Chúc mừng! Đề xuất địa điểm '%s' của bạn đã được phê duyệt."),
    LOCATION_REJECTED("Rất tiếc! Đề xuất địa điểm '%s' của bạn đã bị từ chối."),
    NEW_FOLLOWER("Người dùng '%s' đã bắt đầu theo dõi bạn."),
    NEW_REVIEW("Người dùng '%s' vừa đánh giá địa điểm '%s' của bạn."),
    NEW_FAVORITE("Người dùng '%s' đã thêm địa điểm '%s' của bạn vào danh sách yêu thích."),
    LOCATION_EDIT_APPROVED("Đề xuất chỉnh sửa địa điểm '%s' của bạn đã được duyệt."),
    LOCATION_EDIT_REJECTED("Đề xuất chỉnh sửa địa điểm '%s' của bạn đã bị từ chối."),
    NEW_COMMENT("Người dùng '%s' vừa bình luận về địa điểm '%s' của bạn."),
    NEW_LOCATION_PENDING("Người dùng '%s' vừa đề xuất địa điểm mới: '%s'."),
    LOCATION_EDIT_PENDING("Người dùng '%s' vừa gửi yêu cầu chỉnh sửa cho địa điểm: '%s'."),
    LOCATION_UPDATED_BY_ADMIN("Quản trị viên đã cập nhật thông tin địa điểm '%s' của bạn.");
    private final String template;

    NotificationType(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(this.template, args);
    }
}