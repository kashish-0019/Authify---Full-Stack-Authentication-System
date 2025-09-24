import { createContext, useEffect, useState } from "react";
import { AppConstants } from "../util/constants";
import { toast } from "react-toastify";
import axios from "axios";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
    // Ensure cookies are sent with every request
    axios.defaults.withCredentials = true;

    const backendUrl = AppConstants.BACKEND_URL;
    const [isLoggedin, setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState(null);

    const getUserData = async () => {
        try {
            const response = await axios.get(`${backendUrl}/profile`);
            if (response.status === 200) {
                setUserData(response.data);
            } else {
                toast.error("Unable to retrieve the profile");
            }
        } catch (error) {
            toast.error(error.message);
        }
    };

    const getAuthState = async () => {
        try {
            const response = await axios.get(`${backendUrl}/is-authenticated`);
            if (response.status === 200 && response.data === true) {
                setIsLoggedIn(true);
                await getUserData();
            } else {
                setIsLoggedIn(false);
            }
        } catch (error) {
            // Check for specific statuses and handle them gracefully
            if (error.response) {
                const status = error.response.status;
                if (status === 401 || status === 403) {
                    // This is the expected behavior for an unauthenticated user, do not show an error toast
                } else {
                    const msg = error.response.data?.message || "Authentication check failed";
                    toast.error(msg);
                }
            } else {
                toast.error(error.message);
            }
            setIsLoggedIn(false);
        }
    };

    useEffect(() => {
        getAuthState();
    }, []);

    const contextValue = {
        backendUrl,
        isLoggedin,
        setIsLoggedIn,
        userData,
        setUserData,
        getUserData,
    };

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    );
};
