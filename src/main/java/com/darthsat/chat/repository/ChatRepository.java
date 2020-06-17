package com.darthsat.chat.repository;

import com.darthsat.chat.entity.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends CrudRepository<Chat, String> {

}
