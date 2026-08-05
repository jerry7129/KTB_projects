import { getServerUrl } from './function.js';

let refreshPromise = null;

export const parseJsonSafe = async response => {
    const contentType = response.headers.get('content-type') || '';
    if (!contentType.includes('application/json')) {
        return null;
    }
    try {
        return await response.json();
    } catch (error) {
        return null;
    }
};

const refreshAccessToken = async () => {
    if (!refreshPromise) {
        refreshPromise = (async () => {
            const response = await fetch(`${getServerUrl()}/token`, {
                method: 'POST',
                credentials: 'include',
            });
            const body = await parseJsonSafe(response);
            const accessToken = body?.data?.accessToken;

            if (!response.ok || !accessToken) {
                localStorage.removeItem('accessToken');
                return false;
            }

            localStorage.setItem('accessToken', accessToken);
            return true;
        })().finally(() => {
            refreshPromise = null;
        });
    }

    return refreshPromise;
};

export const requestJson = async (url, options = {}, allowRefresh = true) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    const response = await fetch(url, options);

    if (response.status === 401 && token && allowRefresh) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
            return requestJson(url, options, false);
        }
    }

    const body = await parseJsonSafe(response);
    return {
        response,
        ok: response.ok,
        status: response.status,
        code: body && body.code ? body.code : null,
        data: body && Object.prototype.hasOwnProperty.call(body, 'data')
            ? body.data
            : null,
        body,
    };
};
