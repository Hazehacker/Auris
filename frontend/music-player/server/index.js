import express from 'express'
import cors from 'cors'
import path from 'path'
import { fileURLToPath } from 'url'
import trackRouter from './routes/track.js'
import userRouter from './routes/user.js'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const app = express()
app.use(cors())
app.use(express.json())
// expose uploads
app.use('/uploads', express.static(path.join(__dirname, 'uploads')))

app.use('/user', userRouter)
app.use('/user/track', trackRouter)

const port = process.env.PORT || 3000
app.listen(port, () => console.log(`Mock server listening on http://localhost:${port}`))