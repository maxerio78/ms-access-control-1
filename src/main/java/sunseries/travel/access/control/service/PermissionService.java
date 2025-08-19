package sunseries.travel.access.control.service;

import static sunseries.travel.access.control.constant.Constant.PERMISSION_PREFIX;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;

import lombok.extern.slf4j.Slf4j;
import sunseries.travel.access.control.repository.PermissionRepository;
import sunseries.travel.access.control.repository.RedisRepository;
import sunseries.travel.library.message.EventNotification;
import sunseries.travel.library.utility.DateUtils;
import sunseries.travel.library.utility.JsonUtil;
import sunseries.travel.model.access.control.Resource;
import sunseries.travel.model.access.control.UserPermission;

@Service
@Slf4j
public class PermissionService {
    public static final String HOTEL_BOOKINGS = "HOTEL_BOOKINGS";
    public static final String VIEW_BOOKING_DECISION_PAGE = "VIEW_BOOKING_DECISION_PAGE";
    @Value("${couchbase.bucket.name}")
    private String bucketName;
    @Value("${redis.cache.ttl}")
    private int redisCacheTtl;
    private CouchbaseTemplate template;
    private PermissionRepository repository;
    private RedisRepository redisRepository;
    private ReentrantLock permissionLock = new ReentrantLock(true);

    @Autowired
    public PermissionService(CouchbaseTemplate template, PermissionRepository repository, 
            RedisRepository redisRepository) {
        this.template = template;
        this.repository = repository;
        this.redisRepository = redisRepository;
    }

    public UserPermission findPermissionByEmail(String email) {
        UserPermission permission = null;
        final String permissionId = PERMISSION_PREFIX + email.toLowerCase();
        String json = redisRepository.getData(permissionId);
        if (StringUtils.isEmpty(json)) {
            final String statement = "select meta().id," + bucketName + ".* from " + bucketName + 
                    " where meta().id='" + permissionId + "'";
            N1qlQuery n1qlQuery = N1qlQuery.simple(statement);
            List<N1qlQueryRow> n1qlQueryRowList = template.queryN1QL(n1qlQuery).allRows();
            if (n1qlQueryRowList.isEmpty()) return null;
            permission = n1qlQueryRowList.stream()
                    .map(n1qlQueryRow -> JsonUtil.fromJson(n1qlQueryRow.toString(), UserPermission.class))
                    .filter(o -> !o.isDeleted())
                    .findFirst()
                    .orElse(null);
            if (permission != null) {
                redisRepository.addData(permissionId, JsonUtil.toJson(permission), redisCacheTtl, TimeUnit.MINUTES);
            }
        } else {
            permission = JsonUtil.fromJson(json, UserPermission.class);
        }
        return permission;
    }

    public UserPermission findOne(String id) {
        UserPermission permission = null;
        try {
            String json = redisRepository.getData(id);
            if (StringUtils.isEmpty(json)) {
                Optional<UserPermission> userPermission = this.repository.findById(id);
                if (userPermission.isPresent()) {
                    permission = userPermission.get();
                    redisRepository.addData(id, JsonUtil.toJson(permission), redisCacheTtl, TimeUnit.MINUTES);
                }
            } else {
                permission = JsonUtil.fromJson(json, UserPermission.class);
            }
        } catch (Exception ex) {
            log.error("Exception getting permission for id {}", id, ex);
        }
        return permission;
    }

    public void create(UserPermission userPermission) {
        this.repository.getCouchbaseOperations().insert(userPermission);
    }

    public void update(UserPermission userPermission) {
        this.repository.getCouchbaseOperations().update(userPermission);
        redisRepository.removeData(PERMISSION_PREFIX + userPermission.getEmail().toLowerCase());
    }

    public void delete(UserPermission userPermission) {
        userPermission.setDeleted(true);
        userPermission.setLastUpdatedDate(DateUtils.currentISODateWithUTC());
        this.repository.getCouchbaseOperations().update(userPermission);
        redisRepository.removeData(PERMISSION_PREFIX + userPermission.getEmail().toLowerCase());
    }

    public void hardDelete(UserPermission userPermission) {
        this.repository.delete(userPermission);
        redisRepository.removeData(PERMISSION_PREFIX + userPermission.getEmail().toLowerCase());
    }

    public void delete(UserPermission userPermission, EventNotification eventNotification) {
        userPermission.setDeleted(true);
        userPermission.setLastUpdatedBy(eventNotification.getUserId());
        userPermission.setLastUpdatedDate(DateUtils.currentISODateWithUTC());
        this.repository.getCouchbaseOperations().update(userPermission);
        redisRepository.removeData(PERMISSION_PREFIX + userPermission.getEmail().toLowerCase());
    }

    public void createUpdateUserPermission(final String userEmail, final String userId) {
        permissionLock.lock();
        final String userPermissionId = PERMISSION_PREFIX + userEmail.toLowerCase();
        try {
            final UserPermission currentUserPermission = findOne(userPermissionId);
            if (currentUserPermission != null) {
                updatePermission(currentUserPermission);
            } else {
                final List<Resource> resources = new ArrayList<>();
                generateNewResources(resources);
                final UserPermission userPermission = new UserPermission();
                userPermission.setId(userPermissionId);
                userPermission.setResources(resources);
                userPermission.setEmail(userEmail);
                userPermission.setCreatedDate(DateUtils.currentISODateWithUTC());
                userPermission.setCreatedBy(userId);
                create(userPermission);
            }
        } finally {
            permissionLock.unlock();
        }
    }

    private void updatePermission(UserPermission currentUserPermission) {
        final List<Resource> resources = currentUserPermission.getResources();
        boolean update = false;
        if (resources.isEmpty()) {
            generateNewResources(resources);
            update = true;
        } else {
            for (Resource resource : resources) {
                if (HOTEL_BOOKINGS.equalsIgnoreCase(resource.getName())) {
                    if (!resource.getPermissions().contains(VIEW_BOOKING_DECISION_PAGE)) {
                        resource.getPermissions().add(VIEW_BOOKING_DECISION_PAGE);
                        update = true;
                    }
                }
            }
        }
        if (update) {
            update(currentUserPermission);
        }
    }

    private void generateNewResources(List<Resource> resources) {
        final List<String> permissions = new ArrayList<>();
        permissions.add(VIEW_BOOKING_DECISION_PAGE);
        final Resource resource = new Resource();
        resource.setName(HOTEL_BOOKINGS);
        resource.setPermissions(permissions);
        resources.add(resource);
    }
}
