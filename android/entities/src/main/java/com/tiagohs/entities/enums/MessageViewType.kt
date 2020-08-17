package com.tiagohs.entities.enums

enum class MessageViewType(val type: Int) {
    ERROR(1),
    SUCCESS(2);

    companion object {

        fun getMessageViewType(value: Int?): MessageViewType {
            var contentRating = MessageViewType.ERROR

            for (current in values()) {
                if (current.type == value) {
                    contentRating = current
                    break
                }
            }

            return contentRating
        }
    }
}
