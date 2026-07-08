import { getServerUrl } from '../utils/function.js';
import { requestJson } from '../utils/request.js';

export const getPosts = (startingAfter, limit) => {
    const params = new URLSearchParams({ limit });
    if (startingAfter) {
        params.append('startingAfter', startingAfter);
    }
    const result = requestJson(
        `${getServerUrl()}/posts?${params.toString()}`,
        {
            credentials: 'include',
        },
    );
    return result;
};

export const searchPosts = (keyword, startingAfter = null, limit = 5, sort = 'recent') => {
    const params = new URLSearchParams({
        keyword,
        limit,
        sort,
    });
    if (startingAfter) {
        params.append('startingAfter', startingAfter);
    }
    const result = requestJson(
        `${getServerUrl()}/posts/search?${params.toString()}`,
        {
            credentials: 'include',
        },
    );
    return result;
};
