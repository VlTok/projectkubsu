package com.kubsu.project.service;

import com.kubsu.project.models.Role;
import com.kubsu.project.models.User;
import com.kubsu.project.repos.UserRepository;
import com.kubsu.project.utils.RandomPasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${hostname}")
    private String hostname;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.findByUsername(username);

        if (user==null) {
            throw new UsernameNotFoundException("Пользователь был не найден!");
        }

        return user;
    }
    public User findById(Long id){
        return userRepository.findById(id).get();
    }

    public User findByUsernameAndEmail(String username,String email){ return userRepository.findByUsernameAndEmail(username,email);}

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean addUser(User user) {

        if (!existsUser(user)){
             return false;
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.STUDENT));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        sendMessage(user);
        return true;
    }

    public void recoverUser(User user){
        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String password = randomPasswordGenerator.generatePassayPassword();

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        sendRecoverMessage(user,password);
    }

    public boolean existsUser(User user){
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb!=null){
            return false;
        }

        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if(user==null){
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);

        userRepository.save(user);

        return true;
    }

    private void sendMessage(User user) {
        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Привет, %s!\n"
                            +"Приветствую тебя в веб-приложении <<Расписание Кубанского государственного университета>>. Пожалуйста, для потдверждения своей почты перейдите по ссылке: http://%s/activate/%s",
                    user.getUsername(),
                    hostname,
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(),"Activation code", message);
        }
    }

    private void sendRecoverMessage(User user, String password){
        if (!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Привет, %s!\n" + "Это письмо для восстановления доступа к аккаунту. Ваш новый пароль: %s . После входа в аккаунт по возможности поменяйте его!!!",
                    user.getUsername(),
                    password
            );
            mailSender.send(user.getEmail(),"Восстановление доступа к аккаунту",message);
        }
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles= Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();

        for(String key: form.keySet()){
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
    }

    public void updateProfile(User user, String password, String email, String username) {
        String userEmail = user.getEmail();
        String userUsername = user.getUsername();

        boolean isEmailChanged = isUsernameOrEmailChanged(userEmail,email);
        if (isUsernameOrEmailChanged(userUsername,username)){
            user.setUsername(username);
        }
        if (isEmailChanged){
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }
        if (!StringUtils.isEmpty(password)){
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(user);

        if(isEmailChanged) {
            sendMessage(user);
        }
    }
    public boolean isUsernameOrEmailChanged(String userUsernameOrEmail, String usernameOrEmail){
        return ((usernameOrEmail!=null&&!usernameOrEmail.equals(userUsernameOrEmail))||(userUsernameOrEmail!=null&&!userUsernameOrEmail.equals(usernameOrEmail)));
    }
}
