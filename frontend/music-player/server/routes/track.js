import express from 'express'
import multer from 'multer'
import path from 'path'
import { fileURLToPath } from 'url'
import { ensureAuth } from '../middleware/auth.js'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, path.join(__dirname, '..', 'uploads'))
  },
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname)
    const name = Date.now() + '-' + Math.random().toString(36).slice(2,8) + ext
    cb(null, name)
  }
})
const upload = multer({ storage })

const router = express.Router()

// POST /user/track/playlist
router.post('/playlist', ensureAuth, upload.single('file'), (req, res) => {
  const { playlistId, orderIndex, title, artist, album, coverUrl } = req.body
  if (!playlistId || !title || !artist) {
    return res.status(400).json({ code: 400, msg: 'playlistId、title、artist 为必填', data: null })
  }

  let fileUrl = null
  if (req.file) {
    fileUrl = `${req.protocol}://${req.get('host')}/uploads/${req.file.filename}`
  }

  // For a real app you'd save to DB. For now return mock response
  return res.json({ code: 200, msg: '', data: fileUrl || '' })
})

export default router