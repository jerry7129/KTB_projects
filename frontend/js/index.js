import BoardItem from '../component/board/boardItem.js';
import Dialog from '../component/dialog/dialog.js';
import Header from '../component/header/header.js';
import { authCheck, getServerUrl, prependChild, resolveImageUrl } from '../utils/function.js';
import { getPosts } from '../requests/indexRequest.js';

const DEFAULT_PROFILE_IMAGE = '../public/image/profile/default.jpg';
const HTTP_NOT_AUTHORIZED = 401;
const SCROLL_THRESHOLD = 0.9;
const INITIAL_OFFSET = 5;
const ITEMS_PER_LOAD = 5;
const DEFAULT_SORT = 'recent';
const DEFAULT_ORDER = 'desc';
let currentKeyword = '';
let currentSort = DEFAULT_SORT;
let currentOrder = DEFAULT_ORDER;
let nextCursor = null;
let isEnd = false;
let isProcessing = false;

const updateSortVisibility = () => {
    const sortRow = document.querySelector('#searchSortRow');
    if (!sortRow) return;
    const isSearching = currentKeyword.trim().length > 0;
    sortRow.classList.toggle('isHidden', !isSearching);
    sortRow.setAttribute('aria-hidden', String(!isSearching));
};

// getBoardItem 함수
const getBoardItem = async (cursorValue = null, limitValue = 5) => {
    const result = await getPosts(currentKeyword, cursorValue, limitValue, currentSort, currentOrder);
    if (!result.ok) {
        throw new Error('Failed to load post list.');
    }
    return result.data;
};

const setBoardItem = boardData => {
    const boardList = document.querySelector('.boardList');
    if (boardList && boardData) {
        const itemsHtml = boardData
            .map(data =>
                BoardItem(
                    data.postId,
                    data.createdAt,
                    data.postTitle,
                    data.viewCount,
                    data.postWriter ? data.postWriter.postWriterProfileImageUrl : null,
                    data.postWriter ? data.postWriter.postWriterNickname : null,
                    data.commentCount,
                    data.likeCount,
                ),
            )
            .join('');
        boardList.innerHTML += ` ${itemsHtml}`;
    }
};

const resetBoardList = () => {
    const boardList = document.querySelector('.boardList');
    if (boardList) {
        boardList.innerHTML = '';
    }
};

const loadBoardItems = async ({ reset = false } = {}) => {
    if (isProcessing || (!reset && isEnd)) return;
    isProcessing = true;

    try {
        if (reset) {
            nextCursor = null;
            isEnd = false;
            resetBoardList();
        }
        const cursorData = await getBoardItem(nextCursor, ITEMS_PER_LOAD);
        const items = cursorData.posts;

        if (!items || items.length === 0) {
            isEnd = true;
            return;
        }
        setBoardItem(items);
        
        if (!cursorData.hasNext) {
            isEnd = true;
        } else {
            nextCursor = cursorData.startingAfter;
        }
    } catch (error) {
        console.error('Error fetching items:', error);
        isEnd = true;
    } finally {
        isProcessing = false;
    }
};

const addSearchEvent = () => {
    const searchInput = document.querySelector('#searchInput');
    const searchButton = document.querySelector('.searchButton');
    if (!searchInput || !searchButton) return;

    const runSearch = async () => {
        const trimmedKeyword = searchInput.value.trim();
        if (trimmedKeyword.length > 0 && trimmedKeyword.length < 2) {
            Dialog('검색 실패', '검색어는 2글자 이상 입력해주세요.');
            return;
        }
        currentKeyword = trimmedKeyword;
        updateSortVisibility();
        await loadBoardItems({ reset: true });
    };

    searchButton.addEventListener('click', runSearch);
    searchInput.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            event.preventDefault();
            runSearch();
        }
    });
};

const addSortEvent = () => {
    const sortSelect = document.querySelector('#searchSortSelect');
    if (!sortSelect) return;
    sortSelect.value = `${currentSort}_${currentOrder}`;

    sortSelect.addEventListener('change', async () => {
        const value = sortSelect.value || `${DEFAULT_SORT}_${DEFAULT_ORDER}`;
        const [sort, order] = value.split('_');
        currentSort = sort;
        currentOrder = order;
        if (currentKeyword.trim().length === 0) return;
        await loadBoardItems({ reset: true });
    });
};

// 스크롤 이벤트 추가
const addInfinityScrollEvent = () => {
    window.addEventListener('scroll', async () => {
        const hasScrolledToThreshold =
            window.scrollY + window.innerHeight >=
            document.documentElement.scrollHeight * SCROLL_THRESHOLD;
        if (hasScrolledToThreshold) {
            loadBoardItems();
        }
    });
};

const init = async () => {
    try {
        const response = await authCheck();
        const data = await response.json();
        if (response.status === HTTP_NOT_AUTHORIZED) {
            window.location.href = '/html/login.html';
            return;
        }

        const profileImageUrl = resolveImageUrl(
            data.data.profileImageUrl,
            DEFAULT_PROFILE_IMAGE,
        );

        prependChild(
            document.body,
            Header('Community', 0, profileImageUrl),
        );

        updateSortVisibility();
        await loadBoardItems({ reset: true });

        addSearchEvent();
        addSortEvent();
        addInfinityScrollEvent();
    } catch (error) {
        console.error('Initialization failed:', error);
    }
};

init();
