package com.beyondrest.SegreteriaVirtualegRPC.studente;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudenteService {

    StudenteRepository studenteRepository;

    public StudenteService(StudenteRepository studenteRepository) {
        this.studenteRepository = studenteRepository;
    }

    public Optional<Studente> loadUserByUsername(String username) throws UsernameNotFoundException {
        return studenteRepository.findByUsername(username);
    }
}
