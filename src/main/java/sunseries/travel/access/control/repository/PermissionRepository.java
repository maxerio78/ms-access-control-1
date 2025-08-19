package sunseries.travel.access.control.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import sunseries.travel.model.access.control.UserPermission;

public interface PermissionRepository extends CouchbaseRepository<UserPermission, String> {

}
