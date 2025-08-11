package com.cleverdev.oldsystem.repository;

import com.cleverdev.oldsystem.model.ClientProfile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, String> {

}
