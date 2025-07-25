package com.OLearning.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return error;
    }

    // Xử lý Exception cho giao diện người dùng (trả về view)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, WebRequest request, HttpServletRequest httpRequest) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", ex.getMessage());
        String referer = httpRequest.getHeader("Referer");
        mav.addObject("lastUrl", referer);
        mav.setViewName("error/global-error"); // Tạo view này để hiển thị lỗi chung
        return mav;
    }

    // Xử lý 404 cho giao diện người dùng
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error/404"); // Tạo view này để hiển thị lỗi 404
        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "File size exceeds the allowed limit (maximum 40MB). Please choose a smaller file!");
        String referer = request.getHeader("Referer");
        mav.addObject("lastUrl", referer);
        mav.setViewName("error/global-error");
        return mav;
    }
    
} 