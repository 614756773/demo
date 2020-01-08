package com.hotpot.test;

import com.hotpot.ioc.annotation.Autowired;
import com.hotpot.ioc.annotation.Component;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
@Component
public class StudentController {
    @Autowired
    private StudentService service;

    public String getStudent(String name) {
        return service.get(name);
    }

    public void saveStudent(String name) {
        service.save(name);
    }

}
