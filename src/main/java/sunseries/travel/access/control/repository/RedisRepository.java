package sunseries.travel.access.control.repository;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import sunseries.travel.constant.Origin;
import sunseries.travel.library.repository.CustomRedisRepository;

@Repository
@DependsOn(value = {"redisTemplate"})
public class RedisRepository extends CustomRedisRepository {

    public RedisRepository() {
        super(Origin.MS_ACCESS_CONTROL);
    }

}
