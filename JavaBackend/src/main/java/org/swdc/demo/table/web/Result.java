package org.swdc.demo.table.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 标准的Controller返回结果。
 * @param <T>
 */
@AllArgsConstructor
public class Result<T> {

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private T data;

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(Status.Success,message,data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.Success,"",data);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<>(Status.Failed,message,null);
    }

}
