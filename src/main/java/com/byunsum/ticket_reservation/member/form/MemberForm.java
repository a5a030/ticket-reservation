package com.byunsum.ticket_reservation.member.form;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberForm {
    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "비밀번호")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
