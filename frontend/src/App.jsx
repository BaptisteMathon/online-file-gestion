import { useState, useEffect } from 'react'
import axios from 'axios'

function App() {
  const [listFich, setListFich] = useState([])
  const [fichSel, setFichSel] = useState(null)
  const [isCo, setIsCo] = useState(false)

  useEffect(() => {
    recupFich()
  }, [])

  const recupFich = () => {
    axios.get('http://localhost:8081/api/my-files', { withCredentials: true })
      .then(res => {
        setListFich(res.data)
        setIsCo(true)
      })
      .catch(() => {
        setIsCo(false)
      })
  }

  const lgn = () => {
    window.location.href = 'http://localhost:8081/oauth2/authorization/keycloak'
  }

  const chx = (e) => {
    setFichSel(e.target.files[0])
  }

  const ajt = () => {
    if (!fichSel) return
    const frm = new FormData()
    frm.append('fich', fichSel)

    axios.post('http://localhost:8081/api/my-files', frm, { withCredentials: true })
      .then(() => {
        recupFich()
        setFichSel(null)
      })
      .catch(err => console.error(err))
  }

  const sup = (idF) => {
    axios.delete(`http://localhost:8081/api/my-files/${idF}`, { withCredentials: true })
      .then(() => {
        recupFich()
      })
      .catch(err => console.error(err))
  }

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h1>Mon Espace Fichiers</h1>
      
      {!isCo ? (
        <button onClick={lgn} style={{ padding: '10px 20px', cursor: 'pointer' }}>
          Se connecter
        </button>
      ) : (
        <div>
          <div style={{ marginBottom: '30px', padding: '15px', border: '1px solid #ccc' }}>
            <input type="file" onChange={chx} />
            <button onClick={ajt} style={{ marginLeft: '10px', padding: '5px 15px' }}>
              Uploader le fichier
            </button>
          </div>
          
          <h2>Mes fichiers :</h2>
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {listFich.map(f => (
              <li key={f.id} style={{ marginBottom: '15px', padding: '10px', backgroundColor: '#f9f9f9', display: 'flex', alignItems: 'center' }}>
                <strong style={{ marginRight: '10px' }}>{f.nameFich}</strong> 
                <span style={{ color: 'gray', marginRight: 'auto' }}>({f.typeFich})</span>
                <button onClick={() => sup(f.id)} style={{ color: 'red', cursor: 'pointer', padding: '5px 10px' }}>
                  Supprimer
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}

export default App