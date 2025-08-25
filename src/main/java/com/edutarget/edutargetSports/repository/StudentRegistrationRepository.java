package com.edutarget.edutargetSports.repository;

import com.edutarget.edutargetSports.entity.StudentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRegistrationRepository
        extends JpaRepository<StudentRegistration, Long>, JpaSpecificationExecutor<StudentRegistration> {

//    boolean existsDuplicate(@Param("name") String name,
//                            @Param("classLabel") String classLabel,
//                            @Param("size") Integer jerseySize,
//                            @Param("jerseyNumber") String jerseyNumber,
//                            @Param("excludeId") Long excludeId);

    @Query("""
                select (count(s) > 0) from StudentRegistration s
                 where lower(s.studentName) = lower(:name)
                   and upper(s.studentClassLabel) = upper(:classLabel)
                   and s.jerseySize = :size
                   and s.jerseyNumber = :jerseyNumber
                   and (:excludeId is null or s.id <> :excludeId)
            """)
    boolean existsDuplicate(@Param("name") String name,
                            @Param("classLabel") String classLabel,
                            @Param("size") Integer jerseySize,
                            @Param("jerseyNumber") String jerseyNumber,
                            @Param("excludeId") Long excludeId);

}
