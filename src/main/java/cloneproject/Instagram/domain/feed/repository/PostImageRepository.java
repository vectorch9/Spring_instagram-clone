package cloneproject.Instagram.domain.feed.repository;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;

import cloneproject.Instagram.domain.feed.repository.jdbc.PostImageRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageRepositoryJdbc {

	List<PostImage> findAllByPostId(Long postId);

	List<PostImage> findAllByPost(Post post);
}
