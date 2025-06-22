package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/mnInstructors")
public class InstructorMnController {

    private final Long roleInstructor = 3L;

    @Autowired
    private UserService userService;

    @GetMapping()
    public String getInstructorPage(
            Model model,
            @RequestParam(required = false,defaultValue = "0") int page) {

        Pageable prs = PageRequest.of(page, 5);
        Page<UserDTO> listInstructor = userService.getUsersByRoleWithPagination(roleInstructor,prs);
        List<UserDTO> listUser = listInstructor.getContent();

        Map<Long, Integer> userStudentCountMap = new HashMap<>();


        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listInstructor.getTotalPages());
        model.addAttribute("totalItems", listInstructor.getTotalElements());

        model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorListContent :: instructorListContent");
        model.addAttribute("listInstructor", listUser);
        model.addAttribute("userStudentCountMap", userStudentCountMap);
        model.addAttribute("accNamePage", "Management Instructor");
        model.addAttribute("addAccount", new UserDTO());
        model.addAttribute("listRole", userService.getListRole());
        return "adminDashBoard/index";
    }

    @GetMapping("/request")
    public String getRequestInstrutor(Model model){
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorRequestContent :: instructorRequestContent");
        return "adminDashBoard/index";
    }
}