import { createContext, useEffect, useState } from "react";
import { AppConstants } from "../util/constants";
import { toast } from "react-toastify";
import axios from "axios";

// Helper to safely build API URLs
const apiUrl = (endpoint) => {
  return `${AppConstants.BACKEND_URL.replace(/\/$/, "")}/${endpoint.replace(/^\//, "")}`;
};

export const AppContext = createContext();

export const AppContextProvider = (props) => {

    axios.defaults.withCredentials = true; // send cookies with requests

    const [isLoggedin, setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState(null);

    const getUserData = async () => {
        try {
            const response = await axios.get(apiUrl("/profile"));
            if (response.status === 200) {
                setUserData(response.data);
            } else {
                toast.error("Unable to retrieve the profile");
            }
        } catch (error) {
            toast.error(error.response?.data?.message || error.message);
        }
    };

    const getAuthState = async () => {
        try {
            const response = await axios.get(apiUrl("/is-authenticated"));
            if (response.status === 200 && response.data === true) {
                setIsLoggedIn(true);
                await getUserData();
            } else {
                setIsLoggedIn(false);
            }
        } catch (error) {
            toast.error(error.response?.data?.message || error.message || "Authentication check failed");
            setIsLoggedIn(false);
        }
    };

    useEffect(() => {
        getAuthState();
    }, []);

    const contextValue = {
        backendUrl: AppConstants.BACKEND_URL,
        isLoggedin, setIsLoggedIn,
        userData, setUserData, getUserData
    };

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    );
};
