package com.example.ipro_hr.service.imp;


import com.example.ipro_hr.payload.UserResDTO;
import com.example.ipro_hr.entity.User;
import com.example.ipro_hr.repository.UserRepository;
import com.example.ipro_hr.service.abs.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final UserRepository userRepository;

//    @Override
//    public ApiResult<UserResDTO> getUserByFullName(String fullName) {
//        User user = userRepository.getUserByFullName(fullName);
//        return ApiResult.success(toDTO(user));
//    }
//    @Override
//    public ApiResult<UserResDTO> getUserByLastNameAndFirstName(String firstName, String lastName) {
//        User user = userRepository.getUserByFistNameAndLastName(firstName, lastName);
//        return ApiResult.success(toDTO(user));
//    }

    public UserResDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResDTO.UserResDTOBuilder userResDTO = UserResDTO.builder();
        userResDTO.id(user.getId());
        userResDTO.fullName(user.getFullName());
        userResDTO.phoneNumber(user.getPhoneNumber());
        userResDTO.profession(user.getProfession());
        userResDTO.location(String.valueOf(user.getLocation()));
        userResDTO.level(user.getLevel());
        userResDTO.portfolioLink(user.getPortfolioLink());
        userResDTO.salary(user.getSalary());
        userResDTO.employmentType(user.getEmploymentType());
        userResDTO.resume(user.getResume());
        return userResDTO.build();
    }

}
