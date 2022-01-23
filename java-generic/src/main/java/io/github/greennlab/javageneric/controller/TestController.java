package io.github.greennlab.javageneric.controller;

import io.github.greennlab.javageneric.model.Test01;
import io.github.greennlab.javageneric.support.spring.Dataset;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test01")
public class TestController {

  @PostMapping
  public Object get(@Dataset List<Test01> test01) {
    return test01;
  }

}
