export function isEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

export function convertDate(date) {
    const formatedDate = date.toLocaleDateString()
    const formattedTime = date.toLocaleTimeString()
    return `${formatedDate} ${formattedTime}`;
}