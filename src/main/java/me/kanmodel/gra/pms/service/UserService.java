package me.kanmodel.gra.pms.service;

import me.kanmodel.gra.pms.dao.UserRepository;
import me.kanmodel.gra.pms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 对前台显示用户 查询分页的业务逻辑
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private static Sort sort = new Sort(Sort.Direction.ASC, "id");

    /**
     * 查询全部列表,并做分页
     *  @param pageNum  开始页数
     * @param pageSize 每页显示的数据条数
     * @return
     */
    public Page<User> selectAll(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<User> users = userRepository.findAllByIsDelete(false,pageable);
        return users;
    }

    public Page<User> selectAllByLogin(int pageNum, int pageSize, String login) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, new Sort(Sort.Direction.ASC, "user_id"));
        Page<User> users = userRepository.findAllByLoginAndIsDelete(login, pageable);
        return users;
    }
}
