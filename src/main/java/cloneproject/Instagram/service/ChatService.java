package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.chat.*;
import cloneproject.Instagram.entity.chat.Room;
import cloneproject.Instagram.entity.chat.RoomMember;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.JoinRoomNotFoundException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.ChatRoomNotFoundException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.chat.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageLikeRepository messageLikeRepository;
    private final MessageImageRepository messageImageRepository;
    private final MemberRepository memberRepository;
    private final JoinRoomRepository joinRoomRepository;

    @Transactional
    public ChatRoomCreateResponse createRoom(String username) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member inviter = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Member guest = memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);

        // TODO: feat(대상과 이미 존재하는 채팅방이 있는지 검증)

        final Room room = roomRepository.save(new Room(inviter));
        // TODO: refactor(batch insert)
        roomMemberRepository.save(new RoomMember(inviter, room));
        roomMemberRepository.save(new RoomMember(guest, room));

        return new ChatRoomCreateResponse(true, room.getId(), List.of(new Opponent(inviter)));
    }


    public ChatRoomInquireResponse inquireRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        final long unseenCount = roomUnreadMemberRepository.countByRoomId(room.getId());
        if (roomUnreadMemberRepository.findByRoomIdAndMemberId(room.getId(), member.getId()).isEmpty())
            return new ChatRoomInquireResponse(false, unseenCount);

        roomUnreadMemberRepository.deleteByRoomIdAndMemberId(room.getId(), member.getId());
        return new ChatRoomInquireResponse(true, unseenCount - 1);
    }

    public JoinRoomDeleteResponse deleteJoinRoom(Long roomId) {
        final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        if (joinRoomRepository.findByMemberIdAndRoomId(member.getId(), room.getId()).isEmpty())
            throw new JoinRoomNotFoundException();
        joinRoomRepository.deleteByMemberIdAndRoomId(member.getId(), room.getId());

        return new JoinRoomDeleteResponse(true);
    }

    /*public Page<JoinRoomDTO> getJoinRooms(int page) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        page = (page == 0 ? 0 : page - 1);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(DESC, "id"));
        return JoinRoomRepository.findJoinRoomDTOPagebyMemberId(member.getId(), pageable);
    }*/
}