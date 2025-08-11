package com.example.demo.controller;

import com.example.demo.other.SysUser;
import com.example.demo.other.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/mybatis")
@RestController
public class MybatisController {

    @Autowired
    private SysUserMapper userMapper;

    @RequestMapping(value = "/mock", method = RequestMethod.POST)
    @ResponseBody
    public void mock() {
        SysUser sysUser = new SysUser();
        sysUser.setName("mock");
        userMapper.insert(sysUser);
        userMapper.selectById(1);
    }

}
