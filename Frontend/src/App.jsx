import { useState } from 'react'
import { Route , Routes} from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import EmailVerify from './pages/EmailVerify'
import ResetPassword from './pages/ResetPassword'
import Home from './pages/Home'
import Login from './pages/Login'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div>

        <ToastContainer/>
        <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/login" element={<Login/>}/>
          <Route path="/email-verify" element={<EmailVerify/>}/>
          <Route path="/reset-password" element={<ResetPassword/>}/>
        </Routes>
      </div>
      
    </>
  )
}

export default App
