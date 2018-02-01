package com.softserveinc.geocitizen.service;

import com.softserveinc.geocitizen.dto.MapDataDTO;
import com.softserveinc.geocitizen.entity.Issue;
import com.softserveinc.geocitizen.entity.MapMarker;
import com.softserveinc.geocitizen.entity.User;
import com.softserveinc.geocitizen.exception.AbstractCitizenException;
import com.softserveinc.geocitizen.exception.BadFieldFormatException;
import com.softserveinc.geocitizen.exception.EntityNotExistException;
import com.softserveinc.geocitizen.repository.IssueTypesRepository;
import com.softserveinc.geocitizen.repository.IssuesRepository;
import com.softserveinc.geocitizen.repository.MapMarkersRepository;
import com.softserveinc.geocitizen.repository.UsersRepository;
import com.softserveinc.geocitizen.service.interfaces.IImageService;
import com.softserveinc.geocitizen.service.interfaces.IIssueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.softserveinc.geocitizen.security.model.AuthorizedUser.getCurrent;

@Service
@Transactional
public class IssueServiceImpl implements IIssueService {

	private static final String OPENED_TYPE = "PROBLEM";

	private final IssuesRepository issuesRepository;
	private final MapMarkersRepository mapMarkersRepository;
	private final UsersRepository usersRepository;
	private final IssueTypesRepository issueTypesRepository;
	private final IImageService imageService;

	@Autowired
	public IssueServiceImpl(IssuesRepository issuesRepository,
	                        MapMarkersRepository mapMarkersRepository,
	                        UsersRepository usersRepository,
	                        IssueTypesRepository issueTypesRepository,
	                        IImageService imageService) {
		this.issuesRepository = issuesRepository;
		this.mapMarkersRepository = mapMarkersRepository;
		this.usersRepository = usersRepository;
		this.issueTypesRepository = issueTypesRepository;
		this.imageService = imageService;
	}

	@Override
	public Issue saveIssue(MapDataDTO dto, MultipartFile image) throws BadFieldFormatException {
		MapMarker marker = mapMarkersRepository.findOne(dto.getMarkerId());
		User user = usersRepository.findOne(getCurrent().getId());
		boolean closed = !dto.getTypeName().equals(OPENED_TYPE);
		Issue.Type type = getTypeByName(dto.getTypeName());

		return issuesRepository.save(Issue.Builder.anIssue()
				.setMapMarker(marker)
				.setTitle(dto.getTitle())
				.setText(dto.getDesc())
				.setAuthor(user)
				.setImage(imageService.parseImage(image))
				.setType(type)
				.setClosed(closed)
				.setCreatedAt(LocalDateTime.now())
				.setUpdatedAt(LocalDateTime.now())
				.build());
	}

	@Override
	public Issue getById(Integer id) {
		return issuesRepository.findById(id).orElseThrow(NullPointerException::new);
	}

	@Override
	public List<Issue> getAllIssueByMapMarker(int mapMarkerId) {
		return issuesRepository.findByMapMarker_Id(mapMarkerId);
	}

	@Override
	public Issue findById(Integer id) throws AbstractCitizenException {
		return issuesRepository.findById(id).orElseThrow(() -> new EntityNotExistException(EntityNotExistException.Entity.ISSUE));
	}

	@Override
	public Page<Issue> findByTitleOrText(String title, String text, String author, Pageable pageable) {
		return issuesRepository.findByTitleContainingOrTextContainingOrAuthor_loginContainingAllIgnoreCase(title, text, author, pageable);
	}

	@Override
	public Page<Issue> findAuthorId(Integer id, Pageable pageable) {
		return issuesRepository.findByAuthor_Id(id, pageable);
	}

	@Override
	public Page<Issue> findAll(Pageable pageable) {
		return issuesRepository.findAll(pageable);
	}

	@Override
	public Integer deleteById(Integer id) throws AbstractCitizenException {
		Issue issue = issuesRepository.findById(id)
				.orElseThrow(() -> new EntityNotExistException(EntityNotExistException.Entity.ISSUE));
		issuesRepository.delete(issue);
		if (issuesRepository.countAllByMapMarker(issue.getMapMarker()) < 1) {
			mapMarkersRepository.delete(issue.getMapMarker());
		}
		return 0;
	}

	@Override
	public Integer setStatus(Boolean flag, Integer id) throws AbstractCitizenException {
		Issue issue = issuesRepository.findById(id).
				orElseThrow(() -> new EntityNotExistException(EntityNotExistException.Entity.ISSUE));

		if (StringUtils.equalsIgnoreCase(issue.getType().getName(), "PROBLEM")) {
			issuesRepository.setStatus(flag, id);
		}
		return 0;
	}

	public Page<Issue> findClosedTrue(Pageable pageable) {
		return issuesRepository.findByClosedTrue(pageable);
	}

	public Page<Issue> findClosedFalse(Pageable pageable) {
		return issuesRepository.findByClosedFalse(pageable);
	}

	private Issue.Type getTypeByName(String type) {
		Issue.Type issueType = issueTypesRepository.getByName(type);

		if (issueType == null) {
			issueType = new Issue.Type();
			issueType.setName(type);
		}
		return issueType;
	}
}