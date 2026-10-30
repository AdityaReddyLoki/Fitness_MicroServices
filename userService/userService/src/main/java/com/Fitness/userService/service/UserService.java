    package com.Fitness.userService.service;

    import com.Fitness.userService.dto.RegisterRequest;
    import com.Fitness.userService.dto.UserResponse;
    import com.Fitness.userService.model.User;
    import com.Fitness.userService.repository.UserRepo;
    import lombok.Data;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    @Service
    @Data
    public class UserService {
        @Autowired
        private UserRepo userRepo;

        public UserResponse register(RegisterRequest request){

            if(userRepo.existsByEmail(request.getEmail())){
                UserResponse userResponse=new UserResponse();
                User secondtuser=userRepo.findByEmail(request.getEmail());

                userResponse.setId(secondtuser.getId());
                userResponse.setEmail(secondtuser.getEmail());
                userResponse.setPassword(secondtuser.getPassword());
                userResponse.setFirstName(secondtuser.getFirstName());
                userResponse.setLastName(secondtuser.getLastName());
                userResponse.setCreatedAt(secondtuser.getCreatedAt());
                userResponse.setUpdatedAt(secondtuser.getUpdatedAt());
                return userResponse;
            }


            User user=new User();

            user.setEmail(request.getEmail());
            user.setKeycloakId(request.getKeycloakId());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

           User firstuser= userRepo.save(user); //this will have the 1st user details in db1;

           UserResponse userResponse=new UserResponse();

           userResponse.setId(firstuser.getId());
            userResponse.setKeycloakId(firstuser.getKeycloakId());
           userResponse.setEmail(firstuser.getEmail());
           userResponse.setPassword(firstuser.getPassword());
           userResponse.setFirstName(firstuser.getFirstName());
           userResponse.setLastName(firstuser.getLastName());
           userResponse.setCreatedAt(firstuser.getCreatedAt());
           userResponse.setUpdatedAt(firstuser.getUpdatedAt());
           return userResponse;
        }
        public UserResponse getUserRepo(String userId){

            User user=userRepo.findById(userId)
                    .orElseThrow(()->new RuntimeException("user not found"));

            UserResponse userResponse=new UserResponse();

            userResponse.setId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setPassword(user.getPassword());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setCreatedAt(user.getCreatedAt());
            userResponse.setUpdatedAt(user.getUpdatedAt());

            return userResponse;
        }

        public Boolean validateUserById(String userId) {

            return userRepo.existsBykeycloakId(userId);
        }
    }
