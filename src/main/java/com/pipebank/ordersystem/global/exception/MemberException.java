package com.pipebank.ordersystem.global.exception;

public class MemberException extends RuntimeException {
    
    public MemberException(String message) {
        super(message);
    }
    
    public MemberException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // 특정 예외 타입들
    public static class MemberNotFoundException extends MemberException {
        public MemberNotFoundException(String memberId) {
            super("존재하지 않는 회원입니다: " + memberId);
        }
    }
    
    public static class DuplicateMemberIdException extends MemberException {
        public DuplicateMemberIdException(String memberId) {
            super("이미 존재하는 회원 ID입니다: " + memberId);
        }
    }
    
    public static class InvalidPasswordException extends MemberException {
        public InvalidPasswordException() {
            super("현재 비밀번호가 올바르지 않습니다");
        }
    }
    
    public static class PasswordMismatchException extends MemberException {
        public PasswordMismatchException() {
            super("새 비밀번호가 일치하지 않습니다");
        }
    }
    
    public static class InactiveMemberException extends MemberException {
        public InactiveMemberException(String memberId) {
            super("비활성화된 회원입니다: " + memberId);
        }
    }
} 