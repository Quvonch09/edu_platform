package uz.sfera.edu_platform.repository;

import uz.sfera.edu_platform.entity.Chat;
import uz.sfera.edu_platform.payload.ChatUserSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select ch from Chat ch " +
            "where (ch.sender = :sender and ch.receiver = :receiver) " +
            "or (ch.sender = :receiver and ch.receiver = :sender) " +
            "order by ch.createdAt asc")
    List<Chat> getFindALlChat(@Param("sender") Long sender, @Param("receiver") Long receiver);

    @Query("select ch from Chat ch " +
            "where ch.sender = :senderOrReceiver " +
            "or ch.receiver = :senderOrReceiver " +
            "order by ch.createdAt desc limit 1")
    Chat lastChat(@Param("senderOrReceiver") Long senderOrReceiver);

    @Query("select ch from Chat ch " +
            "where ch.sender = :sender " +
            "and ch.receiver = :receiver " +
            "order by ch.id limit 1")
    Chat checkChat(@Param("sender") Long sender, @Param("receiver") Long receiver);

    @Transactional
    @Modifying
    @Query("update Chat set isRead = 1 where receiver = :receiver")
    void readForReceiver(@Param("receiver") Long receiver);

    @Transactional
    @Modifying
    @Query("update Chat set isRead = 1 where sender = :sender")
    void readForSender(@Param("sender") Long sender);


    @Query("select u.id as id, u.fullName as name, u.phoneNumber as phone, u.file.id as photo, u.role as roleName from Chat ch " +
            "inner join User u on u.id = ch.sender or u.id = ch.receiver " +
            "where (ch.receiver = :sender or ch.sender = :sender) " +
            "and upper(u.fullName) like concat('%', :firstName, '%') " +
            "group by u.id, u.fullName, u.phoneNumber, u.file.id, u.role")
    List<ChatUserSearch> searchByUser(@Param("sender") Long sender, @Param("firstName") String firstName);


    @Query("select u.id as id, u.fullName as name, u.phoneNumber as phone, " +
            "u.file.id as photo, u.role as roleName, cast(u.chatStatus as string) as status " +
            "from Chat ch " +
            "inner join User u on u.id = ch.sender or u.id =ch.receiver " +
            "where ch.sender = :senderOrReceiver " +
            "or ch.receiver = :senderOrReceiver " +
            "group by u.id, u.fullName, u.phoneNumber, u.file.id, u.role, u.chatStatus")
    List<ChatUserSearch> searchChatUser(@Param("senderOrReceiver") Long senderOrReceiver);

//    @Query("select count(ch.id) from Chat ch " +
//            "where (ch.sender = :sender and ch.receiver = :receiver) " +
//            "or (ch.sender = :receiver and ch.receiver = :sender) ")
//    Integer messageCount(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query("select count(cha.id) from Chat cha " +
            "where cha.isRead = 0 and cha.sender = :sender and cha.receiver = :receiver")
    Integer numberOfUnreadMessages(@Param("sender") Long sender, @Param("receiver") Long receiver);


    List<Chat> findAllByChatGroupId(Long groupId);

    @Transactional
    @Modifying
    @Query("delete from Chat " +
            "where (sender = :sender and receiver = :receiver) " +
            "or (sender = :receiver and receiver = :sender)")
    void deleteChat(@Param("sender") Long sender, @Param("receiver") Long receiver);

    @Transactional
    @Modifying
    @Query(value = "delete from chat ch where ch.chat_group_id=?1", nativeQuery = true)
    void deleteChatByChatGroupId(Long groupId);
}
