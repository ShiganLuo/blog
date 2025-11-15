package com.baofeng.blog.dto;
import com.baofeng.blog.enums.ResultCodeEnum;

/**
 * 通用响应封装类，包含状态码、消息和数据体。
 *
 * @param <T> 响应数据类型
 */
public record ApiResponse<T>(int code, String message, T result) {

    /**
     * 返回一个成功响应，并包含数据内容。
     *
     * @param result 返回给前端的数据体
     * @param <T>    数据类型
     * @return 成功响应对象，状态码为 200
     */
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(ResultCodeEnum.SUCCESS.code(), ResultCodeEnum.SUCCESS.message(), result);
    }

    /**
     * 返回一个无数据的成功响应。
     * <p>适用于仅需要返回成功状态，而无数据返回的接口，如点赞、删除等操作。</p>
     *
     * @param <T> 泛型占位（通常为 Void）
     * @return 成功响应对象，状态码为 200，数据为 null
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResultCodeEnum.SUCCESS.code(), ResultCodeEnum.SUCCESS.message(), null);
    }

    /**
     * 返回一个失败响应，使用枚举中定义的默认错误消息。
     *
     * @param code 错误码枚举对象 {@link ResultCodeEnum}
     * @param <T>  数据类型（通常为 null）
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(ResultCodeEnum code) {
        return new ApiResponse<>(code.code(), code.message(), null);
    }

    /**
     * 返回一个失败响应，允许传入自定义错误消息。
     * <p>适用于需要动态错误提示内容的场景。</p>
     *
     * @param code          错误码枚举对象 {@link ResultCodeEnum}
     * @param customMessage 自定义错误描述
     * @param <T>           数据类型（通常为 null）
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(ResultCodeEnum code, String customMessage) {
        return new ApiResponse<>(code.code(), customMessage, null);
    }

    /**
     * 返回一个通用失败响应，使用自定义状态码和消息。
     * <p>适用于没有定义在 {@link ResultCodeEnum} 中的错误场景。</p>
     *
     * @param code    自定义状态码
     * @param message 自定义消息
     * @param <T>     数据类型（通常为 null）
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
