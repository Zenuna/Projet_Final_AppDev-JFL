package cgg.informatique.jfl.webSocket.dao;

import cgg.informatique.jfl.webSocket.entites.Combat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatDao extends JpaRepository<Combat,String> {
}
