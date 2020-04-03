package com.fateasstring.platform.service;


import com.fateasstring.platform.mapper.MenuMapper;
import com.fateasstring.platform.model.Hr;
import com.fateasstring.platform.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**  */
@Service
public class MenuService {

    @Autowired
    MenuMapper menuMapper;

    public List<Menu> getMenusByHrId(){  /** 登陆信息存储在这里 */
        return menuMapper.getMenusByHrId(((Hr)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

}
