package com.otg.tech.

{{cookiecutter.service_package}}.controller;

import com.otg.tech.{{cookiecutter.service_package}}.domain.request.{{cookiecutter.entity}}Request;
import com.otg.tech.{{cookiecutter.service_package}}.domain.response.{{cookiecutter.entity}}Response;
import com.otg.tech.{{cookiecutter.service_package}}.service.{{cookiecutter.entity}}Service;
import com.otg.tech.rest.commons.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/{{ cookiecutter.entity.lower() }}")
public class {{cookiecutter.entity}}

Controller {

    @Autowired
    private {
        {
            cookiecutter.entity
        }
    } Service {
        {
            cookiecutter.entity.lower()
        }
    } Service;

@PostMapping
public ApiResponse < {{cookiecutter.entity}} Response > create( @Valid @RequestBody {
        {
            cookiecutter.entity
        }
    } Request {
        {
            cookiecutter.entity.lower()
        }
    } Request){
        {
            {
                cookiecutter.entity
            }
        } Response response = this. {
            {
                cookiecutter.entity.lower()
            }
        } Service.create({{cookiecutter.entity.lower()}}Request);
        return ApiResponse.success(response);
    }

}
