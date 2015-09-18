package gov.nist.hit.core.edi.repo;

import gov.nist.hit.core.domain.ConformanceProfile;
import gov.nist.hit.core.edi.domain.EDITestContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EDITestContextRepository extends JpaRepository<EDITestContext, Long> {

  @Query("select tc.conformanceProfile from TestContext tc where tc.id = :id")
  public ConformanceProfile findConformanceProfileByTestContextId(@Param("id") Long id);
}
