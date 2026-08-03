import { getServerUrl } from '../utils/function.js';
import { requestJson } from '../utils/request.js';

export const getPosts = (keyword = '', startingAfter = null, limit = 5, sort = 'recent', order = 'desc') => {
    const params = new URLSearchParams({ limit });
    if (keyword.trim()) {
        params.append('keyword', keyword.trim());
    }
    if (sort) {
        params.append('sort', sort);
    }
    if (order) {
        params.append('order', order);
    }
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
