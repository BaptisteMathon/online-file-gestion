import { useState, useEffect } from 'react'
import axios from 'axios'

function App() {
  const [listFich, setListFich] = useState([])
  const [listPartage, setListPartage] = useState([])
  const [fichSel, setFichSel] = useState(null)
  const [isCo, setIsCo] = useState(false)
  const [nomU, setNomU] = useState('')

  const [listU] = useState(['admin1', 'user1'])
  const [fichPart, setFichPart] = useState(null)
  const [usrCb, setUsrCb] = useState('admin1')

  useEffect(() => {
    recupFich()
  }, [])

  const recupFich = () => {
    axios.get('http://localhost:8081/api/my-files', { withCredentials: true })
      .then(res => {
        setListFich(res.data)
        setIsCo(true)
        recupMoi()
        recupPart()
      })
      .catch(() => {
        setIsCo(false)
      })
  }

  const recupMoi = () => {
    axios.get('http://localhost:8081/api/my-files/me', { withCredentials: true })
      .then(res => setNomU(res.data.nom))
      .catch(err => console.error(err))
  }

  const recupPart = () => {
    axios.get('http://localhost:8081/api/my-files/shared', { withCredentials: true })
      .then(res => setListPartage(res.data))
      .catch(err => console.error(err))
  }

  const lgn = () => {
    window.location.href = 'http://localhost:8081/oauth2/authorization/keycloak'
  }

  const dco = () => {
    window.location.href = 'http://localhost:8081/logout'
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
      .then(() => recupFich())
      .catch(err => console.error(err))
  }

  const btnPart = (idF) => {
    setFichPart(idF)
    setUsrCb(listU[0])
  }

  const valPart = () => {
    axios.post(`http://localhost:8081/api/my-files/${fichPart}/share?cible=${usrCb}`, null, { withCredentials: true })
      .then(() => {
        setFichPart(null)
        alert(`Partagé à ${usrCb}`)
      })
      .catch(err => console.error(err))
  }

  const annPart = () => {
    setFichPart(null)
  }

  const dl = (idF, nomF) => {
    axios.get(`http://localhost:8081/api/my-files/${idF}/dl`, {
      withCredentials: true,
      responseType: 'blob'
    })
    .then(res => {
      const url = window.URL.createObjectURL(new Blob([res.data]))
      const lnk = document.createElement('a')
      lnk.href = url
      lnk.setAttribute('download', nomF)
      document.body.appendChild(lnk)
      lnk.click()
      lnk.remove()
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
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', padding: '15px', backgroundColor: '#eef', borderRadius: '5px' }}>
            <span style={{ fontSize: '1.2em', fontWeight: 'bold', color: '#333' }}>Bonjour {nomU} !</span>
            <button onClick={dco} style={{ padding: '8px 15px', backgroundColor: '#ff4757', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
              Se déconnecter
            </button>
          </div>

          <div style={{ marginBottom: '30px', padding: '15px', border: '1px solid #ccc', borderRadius: '5px' }}>
            <input type="file" onChange={chx} />
            <button onClick={ajt} style={{ marginLeft: '10px', padding: '5px 15px' }}>
              Uploader
            </button>
          </div>
          
          <h2>Mes fichiers :</h2>
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {listFich.map(f => (
              <li key={f.id} style={{ marginBottom: '15px', padding: '10px', backgroundColor: '#f9f9f9', display: 'flex', alignItems: 'center', border: '1px solid #eee', borderRadius: '5px' }}>
                <strong style={{ marginRight: '10px' }}>{f.nameFich}</strong> 
                <span style={{ color: 'gray', marginRight: 'auto' }}>({f.typeFich})</span>
                
                <button onClick={() => dl(f.id, f.nameFich)} style={{ color: 'white', backgroundColor: '#3498db', border: 'none', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px', marginRight: '10px' }}>
                  Ouvrir
                </button>

                {fichPart === f.id ? (
                  <div style={{ marginRight: '10px' }}>
                    <select value={usrCb} onChange={(e) => setUsrCb(e.target.value)} style={{ marginRight: '5px', padding: '4px' }}>
                      {listU.map(u => (
                        <option key={u} value={u}>{u}</option>
                      ))}
                    </select>
                    <button onClick={valPart} style={{ color: 'white', backgroundColor: '#2ed573', border: 'none', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px', marginRight: '5px' }}>OK</button>
                    <button onClick={annPart} style={{ color: 'white', backgroundColor: '#a4b0be', border: 'none', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px' }}>Annuler</button>
                  </div>
                ) : (
                  <button onClick={() => btnPart(f.id)} style={{ color: '#2ed573', border: '1px solid #2ed573', background: 'transparent', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px', marginRight: '10px' }}>
                    Partager
                  </button>
                )}

                <button onClick={() => sup(f.id)} style={{ color: '#ff4757', border: '1px solid #ff4757', background: 'transparent', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px' }}>
                  Supprimer
                </button>
              </li>
            ))}
          </ul>

          <h2 style={{ marginTop: '40px' }}>Partagés avec moi :</h2>
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {listPartage.length === 0 ? <li style={{ color: 'gray' }}>Aucun fichier partagé</li> : listPartage.map(f => (
              <li key={f.id} style={{ marginBottom: '15px', padding: '10px', backgroundColor: '#e1f5fe', display: 'flex', alignItems: 'center', border: '1px solid #b3e5fc', borderRadius: '5px' }}>
                <strong style={{ marginRight: '10px' }}>{f.nameFich}</strong> 
                <span style={{ color: 'gray', marginRight: 'auto' }}>({f.typeFich})</span>
                
                <button onClick={() => dl(f.id, f.nameFich)} style={{ color: 'white', backgroundColor: '#3498db', border: 'none', cursor: 'pointer', padding: '5px 10px', borderRadius: '3px' }}>
                  Ouvrir
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