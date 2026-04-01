package br.univates.repository;

import br.univates.model.users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<users, Long> {

    users findByLogin(String login);
}
