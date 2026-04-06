    package Sb_new_project.demo.service;

    import Sb_new_project.demo.dto.RegisterRequestDTO;
    import Sb_new_project.demo.dto.UpdateUserDTO;
    import Sb_new_project.demo.dto.UserResponseDTO;
    import Sb_new_project.demo.entity.UserEntity;
    import org.springframework.security.core.Authentication;

    import java.util.List;
    import java.util.Optional;

    /**
     * Service interface for managing user operations.
     * <p>
     * Provides methods for:
     * - User registration
     * - Fetching user details
     * - Updating user information
     * - Deleting users
     * </p>
     */
    public interface UserService {


        UserEntity registerUser(RegisterRequestDTO dto);

        List<UserResponseDTO> getUsers(Long userid,String username,String email);

        UserResponseDTO getUserById(Long userid);

        UserResponseDTO getUserByEmail(String email);

        UserResponseDTO getUserByUsername(String username);

        List<UserResponseDTO> getAllUsers();

        UserResponseDTO getMyProfile(Authentication authentication);

        UserResponseDTO updateUserByUsername(String username, UpdateUserDTO dto);

        UserResponseDTO updateMyProfile(Authentication authentication, UpdateUserDTO dto);

        void deleteUserByUsername(String username);
    }