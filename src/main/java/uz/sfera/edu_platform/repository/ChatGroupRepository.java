package uz.sfera.edu_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.sfera.edu_platform.entity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    @Query(value = "SELECT COUNT(ch) > 0 FROM chat_group_members ch WHERE ch.chat_group_id = :chatGroupId AND ch.members_id = :userId",
            nativeQuery = true)
    boolean existsMemberInChat(@Param("chatGroupId") Long chatGroupId, @Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "insert into chat_group_members(chat_group_id, members_id) VALUES (:chatGroupId, :userId)",
            nativeQuery = true)
    void addMemberToChatGroup(Long chatGroupId, Long userId);

    @Transactional
    @Modifying
    @Query(value = "delete from chat_group_members where chat_group_id=?1 and members_id=?2", nativeQuery = true)
    void removeMemberFromChatGroup(Long chatGroupId, Long userId);
}
