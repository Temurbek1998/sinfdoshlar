package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Track;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findTop100ByOrderByCreatedAtDesc();

    /** Trekni o'chirishdan oldin uni barcha "Mening musiqam" ro'yxatlaridan olib tashlash. */
    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "delete from user_tracks where track_id = :id", nativeQuery = true)
    void unlinkFromUsers(@Param("id") Long id);

    @Query("""
            select t from Track t
            where lower(t.title) like lower(concat('%', :q, '%'))
               or lower(t.artist) like lower(concat('%', :q, '%'))
            order by t.createdAt desc
            """)
    List<Track> search(@Param("q") String q);
}
