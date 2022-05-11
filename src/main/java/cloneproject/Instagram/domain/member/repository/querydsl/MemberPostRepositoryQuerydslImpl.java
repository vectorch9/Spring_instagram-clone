package cloneproject.Instagram.domain.member.repository.querydsl;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;
import cloneproject.Instagram.domain.feed.dto.QMemberPostDTO;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.domain.feed.entity.QPost.post;
import static cloneproject.Instagram.domain.feed.entity.QBookmark.bookmark;
import static cloneproject.Instagram.domain.feed.entity.QPostTag.postTag;

@RequiredArgsConstructor
public class MemberPostRepositoryQuerydslImpl implements MemberPostRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<MemberPostDTO> getMemberPostDTOs(String username, Pageable pageable) {
		final List<MemberPostDTO> posts = queryFactory
			.select(new QMemberPostDTO(
				post.id,
				post.postImages.size().gt(1),
				post.comments.size(),
				post.postLikes.size()))
			.from(post)
			.where(post.member.username.eq(username))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(post)
			.where(post.member.username.eq(username))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

	@Override
	public Page<MemberPostDTO> getMemberSavedPostDTOs(Long loginUserId, Pageable pageable) {
		final List<MemberPostDTO> posts = queryFactory
			.select(new QMemberPostDTO(
				bookmark.post.id,
				bookmark.post.postImages.size().gt(1),
				bookmark.post.comments.size(),
				bookmark.post.postLikes.size()))
			.from(bookmark)
			.where(bookmark.member.id.eq(loginUserId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(bookmark.post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(bookmark)
			.where(bookmark.member.id.eq(loginUserId))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

	@Override
	public Page<MemberPostDTO> getMemberTaggedPostDTOs(String username, Pageable pageable) {
		final List<MemberPostDTO> posts = queryFactory
			.select(new QMemberPostDTO(
				postTag.postImage.post.id,
				postTag.postImage.post.postImages.size().gt(1),
				postTag.postImage.post.comments.size(),
				postTag.postImage.post.postLikes.size()))
			.from(postTag)
			.where(postTag.tag.username.eq(username))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(postTag.postImage.post.id.desc())
			.distinct()
			.fetch();

		final long total = queryFactory
			.selectFrom(postTag)
			.where(postTag.tag.username.eq(username))
			.fetchCount();
		return new PageImpl<>(posts, pageable, total);
	}

}
