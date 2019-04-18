package com.weijin.partner.repository;

import com.weijin.partner.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yindwe
 * Created on 2019/1/12
 */
public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    @Query(nativeQuery = true,value = "select * from t_message where from_id = ?1 and to_id = ?2")
    List<Message> findByFromIdAndToId(Long currentId, Long uid);

    List<Message> findByFromIdAndToIdOrderByDateDesc(Long id, Long id1);

    @Query(nativeQuery = true,value = "select * from t_message where from_id = ?1 OR to_id = ?1 GROUP BY from_id,to_id ORDER BY date desc")
    List<Message> findAllChatExpert(Long id);
}
