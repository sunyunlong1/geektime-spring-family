package geektime.spring.data.declarativetransactiondemo;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FooServiceImpl implements FooService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FooService fooService;

    @Override
    @Transactional
    public void insertRecord() {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('AAA')");
    }

    @Override
    @Transactional(rollbackFor = RollbackException.class)
    public void insertThenRollback() throws RollbackException {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('BBB')");
        throw new RollbackException();
    }

    @Override
    public void invokeInsertThenRollback() throws RollbackException {
        //1.此处内部调用会使事务失效
//        insertThenRollback();
        //坑：Spring Aop 同级调用会失效
        //2.如果想使事务生效，有两种方法，第一种将自己注入进来，因为spring其实是创建了一个代理，直接调用代理就可以避免了
//        fooService.insertThenRollback();
        //3.这是第二种方式，获取当前类的代理对象
        FooService o = (FooService)AopContext.currentProxy();
        o.insertThenRollback();
    }
}
